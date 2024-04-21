import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Ad } from '../models/ad.model';
import { AdService } from '../services/ad.service';
import { StorageService } from 'src/app/_services/storage.service';
import { User } from '../../authentication/models/user.model';


@Component({
  selector: 'app-ads-list',
  templateUrl: './ads-list.component.html',
  styleUrls: ['./ads-list.component.css']
})
export class AdsListComponent implements OnInit {
  @Input() ads: Ad[] = [];

  constructor(private adService: AdService, private router: Router, private store: StorageService) { }

  protected user!: User | undefined;
  protected isRecap: boolean = false;
  protected isLoading: boolean = true;

  ngOnInit(): void {
    this.user = this.store.getUser()

    console.log(this.user);

    if (this.router.url === '/home') {
      this.getAllAds();
      console.log('Fetching all ads' )
    } else if (this.router.url === '/my-ads') {
      this.isRecap = true;
      console.log('Fetching user ads for user:', this.user?.username, this.user?.id)
      this.getUserAds();
    }
    else {
      this.ads = this.adService.ads;
    }
  } 

    getAllAds() {
      this.adService.getAllAds().subscribe(
        (ads: Ad[]) => {
          this.ads = ads;
          this.isLoading = false;
        },
        (error) => {
          console.error('Error fetching ads:', error);
        }
      );
    }

    getUserAds() {
      this.adService.getUserAds(this.user?.id ?? '').subscribe(
        (ads: Ad[]) => {
          this.ads = ads;
          console.log('User ads:', ads);
          this.isLoading = false;
        },
        (error) => {
          console.error('Error fetching user ads:', error);
        }
      );
    }

}
