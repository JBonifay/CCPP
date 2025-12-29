import {Component, ViewChild} from '@angular/core';
import {Card} from 'primeng/card';
import {FormsModule} from '@angular/forms';
import {MenuItem, PrimeIcons} from 'primeng/api';
import {CdkDrag} from '@angular/cdk/drag-drop';
import {ContextMenu} from 'primeng/contextmenu';
import {BrainstormIdea} from '../../data/model/brainstorm-idea';

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
export class BrainstormComponent {
  @ViewChild('ideaMenu') ideaMenu!: ContextMenu;
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

  brainstormIdea: BrainstormIdea[] = [
    {
      id: 'b87d868b-8662-4855-b5bf-4a0fcd280948',
      title: 'User Onboarding Flow',
      description: 'Design a clearer onboarding flow with fewer steps, inline tips, and a short product tour to help users reach their first success faster.',
      color: "#78e770"
    },
    {
      id: '7ea2e876-0b82-4824-99da-12a74e717e01',
      title: 'Real-Time Collaboration',
      description: 'Allow multiple users to edit and move cards simultaneously with live cursors and presence indicators.',
      color: "#e77070"
    }
  ];

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

}
