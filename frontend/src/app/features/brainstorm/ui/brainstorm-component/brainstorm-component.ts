import {Component, computed, inject, OnInit, ViewChild} from '@angular/core';
import {Card} from 'primeng/card';
import {FormsModule} from '@angular/forms';
import {MenuItem, PrimeIcons} from 'primeng/api';
import {CdkDrag, CdkDragEnd} from '@angular/cdk/drag-drop';
import {ContextMenu} from 'primeng/contextmenu';
import {BrainstormIdea} from '../../data/model/brainstorm-idea';
import {BrainstormStore} from '../../store/brainstorm.store';

@Component({
  selector: 'app-brainstorm-component',
  imports: [
    Card,
    FormsModule,
    CdkDrag,
    ContextMenu
  ],
  templateUrl: './brainstorm-component.html',
  styleUrl: './brainstorm-component.css',
})
export class BrainstormComponent implements OnInit {

  private readonly STORAGE_KEY = 'brainstorm-positions';
  private readonly brainstormStore = inject(BrainstormStore);

  @ViewChild('ideaMenu') ideaMenu!: ContextMenu;
  selectedIdea!: BrainstormIdea;

  brainstormIdea = computed(() => {
    const stored = localStorage.getItem(this.STORAGE_KEY);
    const positions: Record<string, { x: number; y: number }> = stored ? JSON.parse(stored) : {};
    return this.brainstormStore.ideas().map(i => ({
      ...i,
      position: positions[i.id] ?? {x: 20, y: 20}
    }));
  });

  boardMenuItems: MenuItem[] = [
    {label: 'New', icon: PrimeIcons.PLUS}
  ];

  ideaMenuItems = [
    {
      label: 'Edit',
      icon: PrimeIcons.FILE_EDIT,
      command: () => this.editIdea(this.selectedIdea)
    },
    {
      label: 'Color',
      icon: PrimeIcons.PALETTE,
      items: [
        {
          label: 'Green',
          icon: PrimeIcons.CHECK_CIRCLE,
          command: () => this.editColor(this.selectedIdea, "#78e770")
        },
        {
          label: 'Red',
          icon: PrimeIcons.TIMES_CIRCLE,
          command: () => this.editColor(this.selectedIdea, "#e77070")
        }
      ]
    },
    {
      label: 'Delete',
      icon: PrimeIcons.TRASH,
      command: () => this.deleteIdea(this.selectedIdea)
    }
  ];

  ngOnInit() {
    this.brainstormStore.loadIdeas();
  }

  private cleanUpPositions(positions: Record<string, { x: number; y: number }>) {
    const validIds = new Set(this.brainstormIdea().map(i => i.id));
    let changed = false;
    for (const id of Object.keys(positions)) {
      if (!validIds.has(id)) {
        delete positions[id];
        changed = true;
      }
    }
    if (changed) localStorage.setItem(this.STORAGE_KEY, JSON.stringify(positions));
  }

  onIdeaRightClick(event: MouseEvent, idea: BrainstormIdea) {
    this.selectedIdea = idea;
    this.ideaMenu.show(event);
  }

  private editIdea(selectedIdea: BrainstormIdea) {

  }

  private deleteIdea(selectedIdea: BrainstormIdea) {

  }

  private editColor(selectedIdea: BrainstormIdea, color: string) {
    this.brainstormStore.changeColor(selectedIdea.id, color);
  }

  onDragEnd(event: CdkDragEnd, idea: BrainstormIdea) {
    idea.position = event.source.getFreeDragPosition();
    this.persistPositions();
  }

  private persistPositions() {
    const positions: Record<string, { x: number; y: number }> = {};
    for (const idea of this.brainstormIdea()) {
      if (idea.position) positions[idea.id] = idea.position;
    }
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(positions));
  }

}
