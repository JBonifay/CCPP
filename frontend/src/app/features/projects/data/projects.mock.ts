import {Injectable} from '@angular/core';
import {delay, Observable, of, throwError} from 'rxjs';
import {ProjectsRepository} from './projects.repository';
import {ProjectListItem} from './model/project-list-item';
import {ProjectDetails} from './model/project-details';

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

  private mockProjectDetails: Map<string, ProjectDetails> = new Map([
    ['p1', {
      projectId: 'p1',
      title: 'Website Redesign',
      description: 'Complete overhaul of the company website with modern UI/UX, improved performance, and mobile-first design.',
      status: 'Active',
      startDate: new Date('2025-01-15'),
      endDate: new Date('2025-04-30'),
      budgetItems: [
        {id: 'b1-1', description: 'Design Phase', value: 4000, currency: 'EUR'},
        {id: 'b1-2', description: 'Frontend Development', value: 5000, currency: 'EUR'},
        {id: 'b1-3', description: 'Backend Integration', value: 3000, currency: 'EUR'},
      ],
      participants: [
        {participantId: 'u1', name: 'Alice Martin', email: 'alice@example.com', status: 'ACCEPTED'},
        {participantId: 'u2', name: 'Bob Johnson', email: 'bob@example.com', status: 'ACCEPTED'},
        {participantId: 'u3', name: 'Charlie Brown', email: 'charlie@example.com', status: 'ACCEPTED'},
        {participantId: 'u4', name: 'Diana Prince', email: 'diana@example.com', status: 'INVITED'},
        {participantId: 'u5', name: 'Eve Wilson', email: 'eve@example.com', status: 'ACCEPTED'},
      ],
      notes: [
        {userId: 'u1', content: 'Kickoff meeting scheduled for next Monday'},
        {userId: 'u2', content: 'Design mockups approved by stakeholders'},
      ],
    }],
    ['p2', {
      projectId: 'p2',
      title: 'Mobile App Development',
      description: 'Native iOS and Android application for customer engagement and loyalty program.',
      status: 'Planning',
      startDate: new Date('2025-03-01'),
      endDate: new Date('2025-09-30'),
      budgetItems: [
        {id: 'b2-1', description: 'iOS Development', value: 10000, currency: 'EUR'},
        {id: 'b2-2', description: 'Android Development', value: 10000, currency: 'EUR'},
        {id: 'b2-3', description: 'UI/UX Design', value: 3000, currency: 'EUR'},
        {id: 'b2-4', description: 'QA Testing', value: 2000, currency: 'EUR'},
      ],
      participants: [
        {participantId: 'u6', name: 'Frank Miller', email: 'frank@example.com', status: 'ACCEPTED'},
        {participantId: 'u7', name: 'Grace Lee', email: 'grace@example.com', status: 'ACCEPTED'},
        {participantId: 'u8', name: 'Henry Adams', email: 'henry@example.com', status: 'ACCEPTED'},
        {participantId: 'u9', name: 'Ivy Chen', email: 'ivy@example.com', status: 'INVITED'},
        {participantId: 'u10', name: 'Jack Turner', email: 'jack@example.com', status: 'ACCEPTED'},
        {participantId: 'u11', name: 'Kate Williams', email: 'kate@example.com', status: 'ACCEPTED'},
        {participantId: 'u12', name: 'Leo Garcia', email: 'leo@example.com', status: 'INVITED'},
        {participantId: 'u13', name: 'Mia Robinson', email: 'mia@example.com', status: 'ACCEPTED'},
      ],
      notes: [
        {userId: 'u6', content: 'Tech stack decision: React Native vs Flutter pending'},
        {userId: 'u7', content: 'Wireframes in progress'},
      ],
    }],
    ['p3', {
      projectId: 'p3',
      title: 'Marketing Campaign',
      description: 'Q1 digital marketing campaign across social media, email, and PPC channels.',
      status: 'Completed',
      startDate: new Date('2024-10-01'),
      endDate: new Date('2024-12-31'),
      budgetItems: [
        {id: 'b3-1', description: 'Ad Spend', value: 5000, currency: 'EUR'},
        {id: 'b3-2', description: 'Creative Assets', value: 2000, currency: 'EUR'},
        {id: 'b3-3', description: 'Analytics Tools', value: 1000, currency: 'EUR'},
      ],
      participants: [
        {participantId: 'u14', name: 'Noah Davis', email: 'noah@example.com', status: 'ACCEPTED'},
        {participantId: 'u15', name: 'Olivia Martinez', email: 'olivia@example.com', status: 'ACCEPTED'},
        {participantId: 'u16', name: 'Paul Anderson', email: 'paul@example.com', status: 'ACCEPTED'},
      ],
      notes: [
        {userId: 'u14', content: 'Campaign achieved 150% of target KPIs'},
        {userId: 'u15', content: 'Final report submitted to management'},
      ],
    }],
    ['p4', {
      projectId: 'p4',
      title: 'CRM Integration',
      description: 'Integrate Salesforce CRM with existing ERP and customer support systems.',
      status: 'Active',
      startDate: new Date('2025-01-10'),
      endDate: new Date('2025-05-31'),
      budgetItems: [
        {id: 'b4-1', description: 'Salesforce License', value: 8000, currency: 'EUR'},
        {id: 'b4-2', description: 'Integration Development', value: 6000, currency: 'EUR'},
        {id: 'b4-3', description: 'Data Migration', value: 2500, currency: 'EUR'},
        {id: 'b4-4', description: 'Training', value: 1500, currency: 'EUR'},
      ],
      participants: [
        {participantId: 'u17', name: 'Quinn Taylor', email: 'quinn@example.com', status: 'ACCEPTED'},
        {participantId: 'u18', name: 'Ryan Scott', email: 'ryan@example.com', status: 'ACCEPTED'},
        {participantId: 'u19', name: 'Sarah Thompson', email: 'sarah@example.com', status: 'ACCEPTED'},
        {participantId: 'u20', name: 'Tom Harris', email: 'tom@example.com', status: 'ACCEPTED'},
        {participantId: 'u21', name: 'Uma Patel', email: 'uma@example.com', status: 'INVITED'},
        {participantId: 'u22', name: 'Victor Young', email: 'victor@example.com', status: 'ACCEPTED'},
      ],
      notes: [
        {userId: 'u17', content: 'API mapping document completed'},
        {userId: 'u18', content: 'Test environment ready for UAT'},
      ],
    }],
    ['p5', {
      projectId: 'p5',
      title: 'SEO Optimization',
      description: 'Improve organic search rankings through technical SEO and content optimization.',
      status: 'On Hold',
      startDate: new Date('2025-02-01'),
      endDate: new Date('2025-04-15'),
      budgetItems: [
        {id: 'b5-1', description: 'SEO Tools Subscription', value: 2000, currency: 'EUR'},
        {id: 'b5-2', description: 'Content Creation', value: 3000, currency: 'EUR'},
      ],
      participants: [
        {participantId: 'u23', name: 'Wendy Clark', email: 'wendy@example.com', status: 'ACCEPTED'},
        {participantId: 'u24', name: 'Xavier Reed', email: 'xavier@example.com', status: 'DECLINED'},
      ],
      notes: [
        {userId: 'u23', content: 'Project paused pending budget reallocation'},
      ],
    }],
  ]);

  getAll() {
    return of(this.mockProjects).pipe(
      delay(500) // simulate 500ms delay
    );
  }

  getById(id: string): Observable<ProjectDetails> {
    const project = this.mockProjectDetails.get(id);

    if (project) {
      return of(project).pipe(
        delay(500) // simulate 500ms delay
      );
    } else {
      return throwError(() => new Error(`Project with id ${id} not found`)).pipe(
        delay(500)
      );
    }
  }

}
