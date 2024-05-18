import { Component, Input, OnInit } from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';

import { Ad } from '../models/ad.model';
import { AdService } from '../services/ad.service';



@Component({
  selector: 'app-ad-recap',
  templateUrl: './ad-recap.component.html',
  styleUrls: ['./ad-recap.component.css']
})
export class AdRecapComponent implements OnInit {
  @Input() ad!: Ad;

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  goToAd() {
    this.router.navigateByUrl(`ad/${this.ad.id}`);
  }

}
