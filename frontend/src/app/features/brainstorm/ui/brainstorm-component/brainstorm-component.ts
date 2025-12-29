import {Component, computed, inject, OnInit, ViewChild} from '@angular/core';
import {Card} from 'primeng/card';
import {FormsModule} from '@angular/forms';
import {MenuItem, PrimeIcons} from 'primeng/api';
import {CdkDrag, CdkDragEnd} from '@angular/cdk/drag-drop';
import {ContextMenu} from 'primeng/contextmenu';
import {Dialog} from 'primeng/dialog';
import {InputText} from 'primeng/inputtext';
import {Textarea} from 'primeng/textarea';
import {Button} from 'primeng/button';
import {BrainstormIdea} from '../../data/model/brainstorm-idea';
import {BrainstormStore} from '../../store/brainstorm.store';

@Component({
  selector: 'app-brainstorm-component',
  imports: [
    Card,
    FormsModule,
    CdkDrag,
    ContextMenu,
    Dialog,
    InputText,
    Textarea,
    Button
  ],
  templateUrl: './brainstorm-component.html',
  styleUrl: './brainstorm-component.css',
})
export class BrainstormComponent implements OnInit {

  private readonly STORAGE_KEY = 'brainstorm-positions';
  private readonly brainstormStore = inject(BrainstormStore);

  @ViewChild('ideaMenu') ideaMenu!: ContextMenu;
  selectedIdea!: BrainstormIdea;

  editDialogVisible = false;
  editTitle = '';
  editDescription = '';
  isCreating = false;

  brainstormIdea = computed(() => {
    const stored = localStorage.getItem(this.STORAGE_KEY);
    const positions: Record<string, { x: number; y: number }> = stored ? JSON.parse(stored) : {};
    return this.brainstormStore.ideas().map(i => ({
      ...i,
      position: positions[i.id] ?? {x: 20, y: 20}
    }));
  });

  boardMenuItems: MenuItem[] = [
    {
      label: 'New',
      icon: PrimeIcons.PLUS,
      command: () => this.createIdea()
    }
  ];

  ideaMenuItems = [
    {
      label: 'Edit',
      icon: PrimeIcons.FILE_EDIT,
      command: () => this.editIdea(this.selectedIdea)
    } as MenuItem,
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

  onIdeaRightClick(event: MouseEvent, idea: BrainstormIdea) {
    this.selectedIdea = idea;
    this.ideaMenu.show(event);
  }

  private createIdea() {
    this.isCreating = true;
    this.editTitle = '';
    this.editDescription = '';
    this.editDialogVisible = true;
  }

  editIdea(idea: BrainstormIdea) {
    this.isCreating = false;
    this.selectedIdea = idea;
    this.editTitle = idea.title;
    this.editDescription = idea.description;
    this.editDialogVisible = true;
  }

  saveIdea() {
    if (this.isCreating) {
      this.brainstormStore.createIdea({
        id: crypto.randomUUID(),
        title: this.editTitle,
        description: this.editDescription,
        color: '#e3e3e3'
      });
    } else {
      this.brainstormStore.updateIdea({
        id: this.selectedIdea.id,
        title: this.editTitle,
        description: this.editDescription
      });
    }
    this.editDialogVisible = false;
  }

  private deleteIdea(selectedIdea: BrainstormIdea) {
    this.brainstormStore.deleteIdea(selectedIdea.id);
  }

  private editColor(selectedIdea: BrainstormIdea, color: string) {
    this.brainstormStore.changeColor({id: selectedIdea.id, color: color});
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
