import {BudgetItem} from '../../../../shared/models/budget-item';
import {Participant} from '../../../../shared/models/participant';
import {Note} from '../../../../shared/models/note';

export interface ProjectDetails {
  projectId: string,
  title: string,
  description: string,
  status: string,
  budgetItems: BudgetItem[],
  participants: Participant[],
  notes: Note[],
  startDate: Date,
  endDate: Date
}
