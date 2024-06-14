import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdsListComponent } from './ads-list.component';

describe('AdsListComponent', () => {
  let component: AdsListComponent;
  let fixture: ComponentFixture<AdsListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdsListComponent]
    });
    fixture = TestBed.createComponent(AdsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
