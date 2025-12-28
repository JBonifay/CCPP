import { Injectable } from '@angular/core';
import {delay, of} from 'rxjs';
import { ProjectsRepository } from './projects.repository';
import { ProjectListItem } from './model/project-list-item';

@Injectable({ providedIn: 'root' })
export class ProjectsMock implements ProjectsRepository {

  private mockProjects: ProjectListItem[] = [
    { projectId: 'p1', title: 'Website Redesign', status: 'Active', totalBudget: 12000, participantCount: 5 },
    { projectId: 'p2', title: 'Mobile App Development', status: 'Planning', totalBudget: 25000, participantCount: 8 },
    { projectId: 'p3', title: 'Marketing Campaign', status: 'Completed', totalBudget: 8000, participantCount: 3 },
    { projectId: 'p4', title: 'CRM Integration', status: 'Active', totalBudget: 18000, participantCount: 6 },
    { projectId: 'p5', title: 'SEO Optimization', status: 'On Hold', totalBudget: 5000, participantCount: 2 },
    { projectId: 'p6', title: 'Backend API Refactor', status: 'Active', totalBudget: 15000, participantCount: 4 },
    { projectId: 'p7', title: 'Product Launch', status: 'Planning', totalBudget: 30000, participantCount: 10 },
    { projectId: 'p8', title: 'User Feedback Analysis', status: 'Completed', totalBudget: 4000, participantCount: 3 },
    { projectId: 'p9', title: 'Cloud Migration', status: 'Active', totalBudget: 22000, participantCount: 7 },
    { projectId: 'p10', title: 'Security Audit', status: 'Planning', totalBudget: 10000, participantCount: 5 },
    { projectId: 'p11', title: 'Data Warehouse Setup', status: 'Active', totalBudget: 27000, participantCount: 6 },
    { projectId: 'p12', title: 'Internal Training Program', status: 'Completed', totalBudget: 3500, participantCount: 4 },
    { projectId: 'p13', title: 'Bug Fix Sprint', status: 'Active', totalBudget: 9000, participantCount: 3 },
    { projectId: 'p14', title: 'UI/UX Improvements', status: 'On Hold', totalBudget: 7000, participantCount: 4 },
    { projectId: 'p15', title: 'API Documentation', status: 'Completed', totalBudget: 2500, participantCount: 2 },
    { projectId: 'p16', title: 'Customer Support Portal', status: 'Active', totalBudget: 14000, participantCount: 5 },
    { projectId: 'p17', title: 'Performance Optimization', status: 'Planning', totalBudget: 12000, participantCount: 6 },
    { projectId: 'p18', title: 'Analytics Dashboard', status: 'Active', totalBudget: 16000, participantCount: 7 },
    { projectId: 'p19', title: 'Vendor Management', status: 'Completed', totalBudget: 8000, participantCount: 4 },
    { projectId: 'p20', title: 'Mobile Push Notifications', status: 'Active', totalBudget: 11000, participantCount: 5 },
    { projectId: 'p21', title: 'Content Strategy', status: 'Planning', totalBudget: 6000, participantCount: 3 },
    { projectId: 'p22', title: 'System Monitoring', status: 'Active', totalBudget: 13000, participantCount: 4 },
    { projectId: 'p23', title: 'Customer Survey', status: 'Completed', totalBudget: 3500, participantCount: 2 },
    { projectId: 'p24', title: 'DevOps Pipeline', status: 'Active', totalBudget: 20000, participantCount: 6 },
    { projectId: 'p25', title: 'Subscription Model Launch', status: 'Planning', totalBudget: 18000, participantCount: 5 },
    { projectId: 'p26', title: 'A/B Testing', status: 'Completed', totalBudget: 4500, participantCount: 3 },
    { projectId: 'p27', title: 'Localization Update', status: 'Active', totalBudget: 9000, participantCount: 4 },
    { projectId: 'p28', title: 'Social Media Campaign', status: 'On Hold', totalBudget: 7000, participantCount: 3 },
    { projectId: 'p29', title: 'Subscription Analytics', status: 'Active', totalBudget: 12000, participantCount: 5 },
    { projectId: 'p30', title: 'Knowledge Base Upgrade', status: 'Completed', totalBudget: 5000, participantCount: 3 }
  ];

  getAll() {
    return of(this.mockProjects).pipe(
      delay(500) // simulate 500ms delay
    );
  }

}
