export class ServerMockConnectedUserUtils {
    static homepageAdsMock() {
      cy.intercept('GET', '/api/ads/get/all', {
        statuscode: 200,
        fixture: 'ads.json',
      });
  
      this.mockApiCalls();
  
      cy.visit('/');
    }
  
    private static mockApiCalls() {
      cy.intercept('GET', '/api/ads/get/3', {
        statusCode: 200,
        fixture: 'ad1.json',
      });
  
      cy.intercept('GET', '/api/ads/search*', {
        statusCode: 200,
        fixture: 'searchResponse.json',
      });
  
      cy.intercept('POST', '/api/users/login', {
        statusCode: 200,
        fixture: 'userLoginResponse.json',
      });

      //404 if no ad has been added to favorites
      cy.intercept('GET', '/api/ads/favorites/3e4bcb7c-ce0f-4fa7-8b1d-31b8af8ad11a', {
        statusCode: 404,
      });

      cy.intercept('POST', '/api/ads/favorites/3e4bcb7c-ce0f-4fa7-8b1d-31b8af8ad11a/*', {
        statusCode: 200,
      });

      cy.intercept('GET', 'api/users/3e4bcb7c-ce0f-4fa7-8b1d-31b8af8ad11a', {
        statusCode: 200,
        fixture: 'userLoginResponse.json',
    });

      cy.intercept('GET', '/api/ads/user/3e4bcb7c-ce0f-4fa7-8b1d-31b8af8ad11a', {
        statusCode: 200,
        fixture: 'testUserAd.json',
      }); 

      cy.intercept('POST', 'api/users/logout', {
        statusCode: 200,
      });
    }
  }
  