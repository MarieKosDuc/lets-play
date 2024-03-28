import { Component, Input, OnInit } from '@angular/core';
import { Ad } from '../models/ad.model';
import { AdService } from '../services/ad.service';

@Component({
  selector: 'app-ads-list',
  templateUrl: './ads-list.component.html',
  styleUrls: ['./ads-list.component.css']
})
export class AdsListComponent implements OnInit {
  @Input() ads: Ad[] = [];

  constructor(private adService: AdService) { }

  ngOnInit(): void {
    this.adService.getAllAds().subscribe(
      (ads: Ad[]) => {
        this.ads = ads;
      },
      (error) => {
        console.error('Error fetching ads:', error);
      }
    );  } 

}
