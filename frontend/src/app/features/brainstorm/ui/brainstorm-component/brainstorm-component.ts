import {Component, inject, OnInit, ViewChild} from '@angular/core';
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
  brainstormIdea: BrainstormIdea[] = [];
  selectedIdea!: BrainstormIdea;

  boardMenuItems: MenuItem[] = [
    {label: 'Copy', icon: 'pi pi-copy'},
    {label: 'Rename', icon: 'pi pi-file-edit'}
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
      command: () => this.editColor(this.selectedIdea)
    },
    {
      label: 'Delete',
      icon: PrimeIcons.TRASH,
      command: () => this.deleteIdea(this.selectedIdea)
    }
  ];

  ngOnInit() {
    // Trigger loading (rxMethod returns void)
    this.brainstormStore.loadIdeas();

    // Read the current ideas from the store snapshot
    const stored = localStorage.getItem(this.STORAGE_KEY);
    const positions: Record<string, { x: number; y: number }> = stored ? JSON.parse(stored) : {};

    this.brainstormIdea = this.brainstormStore.ideas().map(i => ({
      ...i,
      position: positions[i.id] ?? { x: 20, y: 20 }
    }));

    this.cleanUpPositions(positions);
  }

  private cleanUpPositions(positions: Record<string, { x: number; y: number }>) {
    const validIds = new Set(this.brainstormIdea.map(i => i.id));
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

  private editColor(selectedIdea: BrainstormIdea) {

  }

  onDragEnd(event: CdkDragEnd, idea: BrainstormIdea) {
    idea.position = event.source.getFreeDragPosition();
    this.persistPositions();
  }

  private persistPositions() {
    const positions: Record<string, { x: number; y: number }> = {};
    for (const idea of this.brainstormIdea) {
      if (idea.position) positions[idea.id] = idea.position;
    }
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(positions));
  }

}
