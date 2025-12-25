import { computed, inject } from '@angular/core';
import { signalStore, withState, withComputed, withMethods, patchState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { ApiService } from '../../../core';
import type { ProjectListItem, ProjectDetail } from '../models/project.model';

interface ProjectState {
  projects: ProjectListItem[];
  selectedProject: ProjectDetail | null;
  loading: boolean;
  error: string | null;
}

const initialState: ProjectState = {
  projects: [],
  selectedProject: null,
  loading: false,
  error: null,
};

export const ProjectStore = signalStore(
  withState(initialState),
  withComputed((store) => ({
    hasProjects: computed(() => store.projects().length > 0),
    projectCount: computed(() => store.projects().length),
    isProjectReady: computed(() => store.selectedProject()?.status === 'READY'),
    canModifyProject: computed(() => {
      const status = store.selectedProject()?.status;
      return status === 'PLANNING';
    }),
    totalBudget: computed(() => {
      const project = store.selectedProject();
      if (!project?.budgetItems.length) return null;

      const currency = project.budgetItems[0]?.amount.currency ?? 'USD';
      const total = project.budgetItems.reduce((sum, item) => sum + item.amount.amount, 0);
      return { amount: total, currency };
    }),
  })),
  withMethods((store, api = inject(ApiService)) => ({
    loadProjects: rxMethod<string>(
      pipe(
        tap(() => patchState(store, { loading: true, error: null })),
        switchMap((workspaceId) =>
          api.get<ProjectListItem[]>(`/workspaces/${workspaceId}/projects`).pipe(
            tap({
              next: (projects) => patchState(store, { projects, loading: false }),
              error: (error) =>
                patchState(store, {
                  loading: false,
                  error: error.message ?? 'Failed to load projects',
                }),
            })
          )
        )
      )
    ),

    loadProject: rxMethod<{ workspaceId: string; projectId: string }>(
      pipe(
        tap(() => patchState(store, { loading: true, error: null })),
        switchMap(({ workspaceId, projectId }) =>
          api.get<ProjectDetail>(`/workspaces/${workspaceId}/projects/${projectId}`).pipe(
            tap({
              next: (project) => patchState(store, { selectedProject: project, loading: false }),
              error: (error) =>
                patchState(store, {
                  loading: false,
                  error: error.message ?? 'Failed to load project',
                }),
            })
          )
        )
      )
    ),

    clearSelectedProject(): void {
      patchState(store, { selectedProject: null });
    },

    clearError(): void {
      patchState(store, { error: null });
    },

    // Optimistic update helpers
    addProjectToList(project: ProjectListItem): void {
      patchState(store, { projects: [...store.projects(), project] });
    },

    updateProjectInList(projectId: string, updates: Partial<ProjectListItem>): void {
      patchState(store, {
        projects: store.projects().map((p) => (p.id === projectId ? { ...p, ...updates } : p)),
      });
    },

    removeProjectFromList(projectId: string): void {
      patchState(store, {
        projects: store.projects().filter((p) => p.id !== projectId),
      });
    },
  }))
);
