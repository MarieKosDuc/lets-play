import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';

import { Subscription } from 'rxjs';

import { MenuItem, MessageService } from 'primeng/api';

import { AuthenticationService } from '../../authentication/services/authentication.service';
import { User } from '../../authentication/models/user.model';
import { AuthStorageService } from '../../shared/services/storage.service';
import { EventBusService } from 'src/app/shared/services/event-bus.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  public showDescription!: boolean;
  public userLoggedIn = false;
  public userInfos!: User | null;
  public items: MenuItem[] | undefined;
  public itemsSmall: MenuItem[] | undefined;
  public itemsSmallConnected: MenuItem[] | undefined;

  private userSubscription: Subscription;

  private eventBusSubscription?: Subscription;

  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private authStorageService: AuthStorageService,
    private eventBusService: EventBusService,
    private messageService: MessageService
  ) {
    this.userSubscription = this.authStorageService.user$.subscribe((user) => {
      this.userInfos = user;
      this.userLoggedIn = !!user;
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

    this.userLoggedIn = this.authStorageService.isLoggedIn();

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
              this.router.navigate(['/profile']);
            },
          },
          {
            label: 'Mes annonces',
            command: () => {
              this.router.navigate(['/my-ads']);
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
              this.router.navigate(['/profile']);
            },
          },
          {
            label: 'Mes annonces',
            command: () => {
              this.router.navigate(['/my-ads']);
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

  logOut() {
    this.authService.logout().subscribe(
      (response) => {
        this.userLoggedIn = false;
        this.userInfos = null;
        this.authStorageService.clean();
        this.messageService.add({ severity: 'info', summary: 'Déconnexion', detail: 'Tu es deconnecté.e' });
        setTimeout(() => {
          this.router.navigate(['/home']);
        }, 1000);
      },
      (error) => {
        console.log(error);
      }
    );
  }
}
