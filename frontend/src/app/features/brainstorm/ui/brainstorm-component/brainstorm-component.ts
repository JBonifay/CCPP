import {Component} from '@angular/core';
import {Card} from 'primeng/card';
import {FormsModule} from '@angular/forms';
import {MenuItem} from 'primeng/api';
import {CdkDrag} from '@angular/cdk/drag-drop';


@Component({
  selector: 'app-brainstorm-component',
  imports: [
    Card,
    FormsModule,
    CdkDrag
  ],
  templateUrl: './brainstorm-component.html',
  styleUrl: './brainstorm-component.css',
})
export class BrainstormComponent {

  menuItems: MenuItem[] = [
    {label: 'Copy', icon: 'pi pi-copy'},
    {label: 'Rename', icon: 'pi pi-file-edit'}
  ];

}
