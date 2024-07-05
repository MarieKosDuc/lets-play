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
    }
  }
  