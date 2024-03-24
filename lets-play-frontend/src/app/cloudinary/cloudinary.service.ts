import { Injectable } from '@angular/core';
import { Cloudinary, CloudinaryConfig } from '@cloudinary/url-gen';

@Injectable({
  providedIn: 'root'
})
export class CloudinaryService {
  
  private cloudinary: Cloudinary;
  private cloudName: string = 'daotbgmy2';

  constructor() {
    this.cloudinary = new Cloudinary({
      cloud: {
        cloudName: this.cloudName,
        apiKey: '397389777628642',
        apiSecret: 'y2712Vwa-M56Cd_nOyR3SyJDNM8'
      }
    });
  }

  getCloudinary(): Cloudinary {
    return this.cloudinary;
  }

  getCloudName(): String {
    return this.cloudName;
  }

}
