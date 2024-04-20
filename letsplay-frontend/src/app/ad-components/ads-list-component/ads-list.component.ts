import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Ad } from '../models/ad.model';
import { AdService } from '../services/ad.service';
import { AuthenticationService } from '../../authentication/services/authentication.service';
import { User } from '../../authentication/models/user.model';


@Component({
  selector: 'app-ads-list',
  templateUrl: './ads-list.component.html',
  styleUrls: ['./ads-list.component.css']
})
export class AdsListComponent implements OnInit {
  @Input() ads: Ad[] = [];

  constructor(private adService: AdService, private router: Router, private authService: AuthenticationService) { }

  protected user!: User | null;

  ngOnInit(): void {

    this.authService.getCurrentUser().subscribe((user: User | null) => {
      this.user = user;
    });

    console.log(this.user);

    if (this.router.url === '/home') {
      this.getAllAds();
    } else if (this.router.url === '/my-ads') {
      console.log('Fetching user ads for user:', this.user?.username)
      this.adService.getUserAds().subscribe(
        (ads: Ad[]) => {
          this.ads = ads;
        },
        (error) => {
          console.error('Error fetching user ads:', error);
        }
      );
    }
    else {
      this.ads = this.adService.ads;
    }
  } 
    getAllAds() {
      this.adService.getAllAds().subscribe(
        (ads: Ad[]) => {
          this.ads = ads;
        },
        (error) => {
          console.error('Error fetching ads:', error);
        }
      );
    }

}
