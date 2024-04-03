import { Component, Input, OnInit } from '@angular/core';
import { Ad } from '../models/ad.model';
import { AdService } from '../services/ad.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-ads-list',
  templateUrl: './ads-list.component.html',
  styleUrls: ['./ads-list.component.css']
})
export class AdsListComponent implements OnInit {
  @Input() ads: Ad[] = [];

  constructor(private adService: AdService, private router: Router) { }

  ngOnInit(): void {
    if (this.router.url === '/home') {
      this.getAllAds();
    } else {
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
