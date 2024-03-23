import { Component, OnInit } from '@angular/core';
import { Ad } from '../models/ad.model';
import { AdService } from '../services/ad.service';

@Component({
  selector: 'app-ads-list',
  templateUrl: './ads-list.component.html',
  styleUrls: ['./ads-list.component.css']
})
export class AdsListComponent implements OnInit {
  ads!: Ad[];

  constructor(private adService: AdService) { }

  ngOnInit(): void {
    this.ads = this.adService.getAllAds();
  } 

}
