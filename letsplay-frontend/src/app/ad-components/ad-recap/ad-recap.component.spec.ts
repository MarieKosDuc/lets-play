import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdRecapComponent } from './ad-recap.component';

describe('AdRecapComponent', () => {
  let component: AdRecapComponent;
  let fixture: ComponentFixture<AdRecapComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdRecapComponent]
    });
    fixture = TestBed.createComponent(AdRecapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
