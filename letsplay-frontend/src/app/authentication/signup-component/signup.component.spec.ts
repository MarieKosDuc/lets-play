import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageService } from 'primeng/api';

import { SignupComponent } from './signup.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthenticationService } from '../services/authentication.service';
import { Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';

describe('SignupComponent', () => {
  let component: SignupComponent;
  let fixture: ComponentFixture<SignupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [SignupComponent],
      providers: [
        { provide: AuthenticationService, useValue: {} },
        { provide: Router, useValue: {} },
        { provide: MessageService, useValue: {} },
        FormBuilder,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SignupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be invalid when empty', () => {
    // @ts-ignore
    expect(component.signupForm.valid).toBeFalsy();
  });

  it('should be invalid when email is in incorrect format', () => {
    component.signupForm.controls['email'].setValue('test');
    // @ts-ignore
    expect(component.signupForm.valid).toBeFalsy();
  });
  
  it('should be invalid when password is too short', () => {
    component.signupForm.controls['password'].setValue('123');
    // @ts-ignore
    expect(component.signupForm.valid).toBeFalsy();
  });
});
