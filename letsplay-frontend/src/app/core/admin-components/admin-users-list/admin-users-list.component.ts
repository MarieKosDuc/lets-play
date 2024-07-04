import { Component, Input } from '@angular/core';
import { User } from 'src/app/authentication/models/user.model';
import { AdminService } from '../../services/admin.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-admin-users-list',
  templateUrl: './admin-users-list.component.html',
  styleUrl: './admin-users-list.component.css',
})
export class AdminUsersListComponent {
  @Input() users: User[] = [];

  constructor(
    private adminService: AdminService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (error) => {
        console.error('Error fetching users:', error);
      },
    });
  }

  protected deleteUser(id: string): void {
    this.adminService.deleteUser(id).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Utilisateur supprimé',
          detail: "L'utilisateur a bien été supprimé",
        });
        this.users = this.users.filter((user) => user.id !== id);
      },
      error: (error) => {
        console.error('Error deleting user:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: 'Une erreur est survenue',
        });
      },
    });
  }
}
