import {UserRole} from './user';

export interface Workspace {
  workspaceId: string;
  workspaceName: string;
  workspaceLogoUrl: string;
  userRole: UserRole;
}
