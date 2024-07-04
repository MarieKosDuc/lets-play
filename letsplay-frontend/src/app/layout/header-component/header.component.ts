import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';

import { Subscription } from 'rxjs';

import { MenuItem, MessageService } from 'primeng/api';

import { AuthenticationService } from '../../authentication/services/authentication.service';
import { User } from '../../authentication/models/user.model';
import { AuthStorageService } from '../../shared/services/storage.service';
import { EventBusService } from 'src/app/shared/services/event-bus.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  protected showDescription!: boolean;
  protected userLoggedIn = false;
  protected userInfos!: User | null;
  protected isAdmin: boolean = false;
  protected items: MenuItem[] | undefined;
  protected itemsSmall: MenuItem[] | undefined;
  protected itemsSmallConnected: MenuItem[] | undefined;
  protected itemsAdminSmall: MenuItem[] | undefined;

  private userSubscription: Subscription;

  private eventBusSubscription?: Subscription;

  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private authStorageService: AuthStorageService,
    private eventBusService: EventBusService,
    private messageService: MessageService,
    private activatedRoute: ActivatedRoute,
    private cd: ChangeDetectorRef
  ) {
    this.userSubscription = this.authStorageService.user$.subscribe({
      next: (user) => {
        this.userInfos = user;
        this.userLoggedIn = !!user;
        if (user) {
          this.isAdmin = user?.roles?.includes('ROLE_ADMIN') ?? false;
        }
      },
    });
  }

  isHomeRoute(): boolean {
    return this.router.url === '/home';
  }

  ngOnInit() {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        const currentRoute = this.router.url;
        this.showDescription = currentRoute === '/home';
      }
    });

    this.authService.userLoggedIn.subscribe((loggedIn) => {
      this.userLoggedIn = loggedIn;
      this.cd.detectChanges();
    });

    this.userLoggedIn = this.authStorageService.isLoggedIn();
    this.userInfos = this.authStorageService.getUser();

    this.eventBusSubscription = this.eventBusService.on('logout', () => {
      console.log('Logout event received');
      this.logOut();
    });

    this.items = [
      {
        items: [
          {
            label: 'Profil',
            command: () => {
              if (this.userInfos && this.userInfos.id) {
                this.router.navigate(['/profile', this.userInfos.id]);
              }
            },
          },
          {
            label: 'Nouvelle annonce',
            command: () => {
              this.router.navigate(['/create-ad']);
            },
          },
          {
            label: 'Favorites',
            command: () => {
              this.router.navigate(['/fav-ads']);
            },
          },
          {
            label: 'Déconnexion',
            command: () => {
              this.logOut();
            },
          },
        ],
      },
    ];

    this.itemsSmall = [
      {
        items: [
          {
            label: 'Rechercher',
            command: () => {
              this.router.navigate(['/search']);
            },
          },
          {
            label: 'Connexion/Inscription',
            command: () => {
              this.router.navigate(['/login']);
            },
          },
        ],
      },
    ];

    this.itemsAdminSmall = [
      {
        items: [
          {
            label: 'Membres',
            command: () => {
              this.router.navigate(['/admin/users']);
            },
          },
          {
            label: 'Annonces',
            command: () => {
              this.router.navigate(['/admin/ads']);
            },
          },
          {
            label: 'Déconnexion',
            command: () => {
              this.logOut();
            },
          },
        ],
      },
    ];

    this.itemsSmallConnected = [
      {
        items: [
          {
            label: 'Rechercher',
            command: () => {
              this.router.navigate(['/search']);
            },
          },
          {
            label: 'Profil',
            command: () => {
              if (this.userInfos && this.userInfos.id) {
                this.router.navigate(['/profile', this.userInfos.id]);
              }
            },
          },
          {
            label: 'Nouvelle annonce',
            command: () => {
              this.router.navigate(['/create-ad']);
            },
          },
          {
            label: 'Favorites',
            command: () => {
              this.router.navigate(['/fav-ads']);
            },
          },
          {
            label: 'Déconnexion',
            command: () => {
              this.logOut();
            },
          },
        ],
      },
    ];
  }

  public logOut() {
    this.authService.logout().subscribe({
      next: () => {
        this.userLoggedIn = false;
        this.userInfos = null;
        this.authStorageService.clean();
        this.eventBusService.emit({name: 'logout', value: 'User logged out'});
        this.router.navigate(['/home']);
        this.messageService.add({
          severity: 'info',
          summary: 'Déconnexion',
          detail: 'Tu es deconnecté.e',
        });
      },
      error: (error) => {
        console.error(error);
        this.eventBusService.emit({name: 'logout', value: 'User logged out'});
        this.router.navigate(['/home']);
      },
    });
  }
}
