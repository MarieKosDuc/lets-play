import { Injectable } from '@angular/core';
import { Cloudinary } from '@cloudinary/url-gen';

@Injectable({
  providedIn: 'root'
})
export class CloudinaryService {
  
  private cloudinary: Cloudinary;
  private cloudName: string = 'daotbgmy2';
  private uploadPreset: string = 'ml_default';
  private baseImageURL: string = 'https://res.cloudinary.com/daotbgmy2/image/upload/v1711275248/';

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

  getUploadPreset(): String { 
    return this.uploadPreset;
  }

  getBaseImageURL(): String {
    return this.baseImageURL;
  }

}
