import { Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Validators } from '@angular/forms';

import { Ad } from '../models/ad.model';
import { AdService } from '../services/ad.service';
import { musicStylesEnum } from '../enums/musicStylesEnum';


@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent {

  seeking!: string;
  searchForm!: FormGroup;
  dropdownList = [...Object.keys(musicStylesEnum).map((key) => ({ item_id: key, item_text: musicStylesEnum[key as keyof typeof musicStylesEnum] }))];
  selectedItems: string[] = [];
  dropdownSettings = {};

  showAds: boolean = false;
  ads!: Ad[];
  resultNumber: number = 0;


  constructor(private formbuilder: FormBuilder, private adService: AdService) {
    this.searchForm = this.formbuilder.group({
      search: ['', Validators.required],
      musicianType: [''],
      location: ['', Validators.required],
      selectedItems: ['', Validators.required]
    });
   }

   ngOnInit() {
    this.selectedItems = [];
    this.dropdownSettings = {
      singleSelection: false,
      idField: 'item_id',
      textField: 'item_text',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      itemsShowLimit: 3,
      allowSearchFilter: true
    };
  }

  setSeeking(value: string) {
    this.seeking = value;
  }

  onItemSelect(item: any) {
    this.selectedItems.push(item.item_text);
    console.log(this.selectedItems);
  }


  onItemDeSelect(item: any) {
    const index = this.selectedItems.indexOf(item.item_text);
    this.selectedItems.splice(index, 1);
    console.log(this.selectedItems);
  }

  onSelectAll(items: any) {
    this.selectedItems = items.map((item: any) => item.item_text);
    console.log(this.selectedItems);
  }

  onDeSelectAll(items: any) {
    this.selectedItems = [];
    console.log(this.selectedItems);
  }

  onSubmit() {
    const metalGenres = Object.keys(musicStylesEnum).filter(key => this.selectedItems.includes(musicStylesEnum[key as keyof typeof musicStylesEnum]));


    this.adService.searchAds.call(this.adService, this.searchForm.value.musicianType, metalGenres, this.searchForm.value.location).subscribe((ads: Ad[]) => {
      this.ads = ads;
      this.resultNumber = ads.length;
      this.showAds = true;
    });
  }
}
