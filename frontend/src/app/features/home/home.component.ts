import {Component} from '@angular/core';
import {TableModule} from 'primeng/table';
import {Card} from 'primeng/card';

@Component({
  selector: 'app-home',
  imports: [
    TableModule,
    Card
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent {

}
