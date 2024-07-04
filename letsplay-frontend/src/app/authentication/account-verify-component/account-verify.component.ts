import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router'; // Import ActivatedRoute module

import { environment } from 'src/environments/environment';

import { AuthenticationService } from '../services/authentication.service';

@Component({
  selector: 'app-account-verify',
  templateUrl: './account-verify.component.html',
  styleUrl: './account-verify.component.css'
})
export class AccountVerifyComponent {
  protected loading: boolean = true;
  protected isValidationSuccess: boolean = false;
  protected mailto: string = environment.contact_email;
  protected token: string = '';

  constructor(
    private authService: AuthenticationService,
    private route: ActivatedRoute // Inject ActivatedRoute module
  ) { }

  ngOnInit(): void {
    this.token = this.route.snapshot.params['token'];
    console.log(this.token)

    this.authService.verifyAccount(this.token).subscribe( {
      next: (response) => {
        this.loading = false;
        this.isValidationSuccess = true;
      },
      error: (error) => {
        this.loading = false;
        this.isValidationSuccess = false;
      }
    });
  }
}
