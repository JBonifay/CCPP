import {Component, inject} from '@angular/core';
import {ProjectDetailsStore} from '../../store/project-details.store';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-project-detail',
  imports: [DatePipe],
  template: `
    @if (project(); as proj) {
      <div>
        <p>{{ proj.projectId }}</p>
        <p>{{ proj.title }}</p>
        <p>{{ proj.description }}</p>
        <p>{{ proj.status }}</p>
        <p>Start: {{ proj.startDate | date }}</p>
        <p>End: {{ proj.endDate | date }}</p>

        <h3>Budget Items:</h3>
        <ul>
          @for (item of proj.budgetItems; track item.id) {
            <li>{{ item.description }}: {{ item.value }} {{ item.currency }}</li>
          }
        </ul>

        <h3>Participants:</h3>
        <ul>
          @for (participant of proj.participants; track participant.participantId) {
            <li>{{ participant.name }}</li>
          }
        </ul>

        <h3>Notes:</h3>
        <ul>
          @for (note of proj.notes; track note.userId) {
            <li>{{ note.content }} - {{ note.userId }}</li>
          }
        </ul>
      </div>
    } @else {
      <p>Loading project...</p>
    }
  `,
})
export class ProjectDetailComponent {

  readonly projectDetailsStore = inject(ProjectDetailsStore);

  protected project() {
    return this.projectDetailsStore.project();
  }

}
