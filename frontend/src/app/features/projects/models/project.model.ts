export type ProjectStatus = 'PLANNING' | 'READY' | 'ACTIVE' | 'CANCELLED' | 'FAILED';
export type InvitationStatus = 'INVITED' | 'ACCEPTED' | 'DECLINED';

export interface Money {
  amount: number;
  currency: string;
}

export interface BudgetItem {
  id: string;
  description: string;
  amount: Money;
}

export interface Participant {
  id: string;
  email: string;
  name: string;
  status: InvitationStatus;
}

export interface Note {
  id: string;
  content: string;
  authorId: string;
  createdAt: string;
}

export interface DateRange {
  startDate: string;
  endDate: string;
}

export interface ProjectListItem {
  id: string;
  title: string;
  status: ProjectStatus;
  timeline: DateRange | null;
}

export interface ProjectDetail {
  id: string;
  title: string;
  description: string;
  status: ProjectStatus;
  timeline: DateRange | null;
  budgetItems: BudgetItem[];
  participants: Participant[];
  notes: Note[];
}
