import { Component } from '@angular/core';
import {Button} from 'primeng/button';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-landing',
  imports: [
    Button,
    RouterLink
  ],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css',
})
export class Landing {

}
