import { Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Validators } from '@angular/forms';
import { Ad } from '../ad-components/models/ad.model';


@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent {

  seeking!: string;
  searchForm!: FormGroup;
  dropdownList = [
    { item_id: 1, item_text: 'Death metal' },
    { item_id: 2, item_text: 'Thrash metal' },
    { item_id: 3, item_text: 'Other' }
  ];;
  selectedItems: string[] = [];
  dropdownSettings = {};

  showAds: boolean = false;
  ads!: Ad[];
  resultNumber: number = 0;


  constructor(private formbuilder: FormBuilder) {
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
    console.log(this.searchForm.value);
    }
}
