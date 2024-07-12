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

  protected visibleSuppressToast: boolean = false;
  protected userIdToDelete: string = '';

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

  protected showConfirmSuppressAccount(userid: string) {
    if (!this.visibleSuppressToast) {
      this.messageService.add({
        key: 'confirm-account-deletion',
        sticky: true,
        severity: 'info',
        summary:
          'Es-tu sûr.e de vouloir supprimer cet utilisateur ? Cette suppression est irréversible.',
      });
      this.visibleSuppressToast = true;
      this.userIdToDelete = userid;
    }
  }

  protected onConfirm() {
    this.deleteUser(this.userIdToDelete);
    this.messageService.clear('confirm-account-deletion');
    this.visibleSuppressToast = false;
  }

  protected onReject() {
    this.messageService.clear('confirm-account-deletion');
    this.visibleSuppressToast = false;
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
