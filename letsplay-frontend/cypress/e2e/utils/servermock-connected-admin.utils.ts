export class ServerMockAdminUtils {
  static mockApiCalls() {
    cy.intercept('POST', '/api/users/login', {
      statusCode: 200,
      fixture: 'adminLoginResponse.json',
    });

    cy.intercept('GET', '/api/admin/users', {
      statusCode: 200,
      fixture: 'users.json',
    });

    cy.intercept('GET', '/api/admin/ads', {
      statusCode: 200,
      fixture: 'ads.json',
    });

    cy.intercept('DELETE', '/api/admin/users/*', {
      statusCode: 200,
    });

    cy.intercept('GET', '/api/ads/get/all', {
      statuscode: 200,
      fixture: 'ads.json',
    });

    cy.intercept('GET', '/api/ads/get/3', {
      statusCode: 200,
      fixture: 'ad3.json',
    });

    cy.intercept('DELETE', '/api/admin/ads/*', {
      statusCode: 200,
    });
  }
}
