import { ServerMockAdminUtils } from '../utils/servermock-connected-admin.utils';

describe('Interface administrateur - gestion des utilisateurs', () => {
  beforeEach(() => {
    ServerMockAdminUtils.mockApiCalls();
    cy.loginAsAdmin('admin@mail.com', 'adminPassword');
  });

  it('Checks that admin can see the list of users', () => {
    cy.visit('/admin/users');
    cy.get('[data-cy=users]').should('be.visible');
    cy.get('[data-cy=users]').children().should('have.length', 3);
  });

  it('Checks that admin can delete a user', () => {
    cy.visit('/admin/users');
    cy.get('[data-cy=users]').children().should('have.length', 3);
    cy.get('[data-cy=delete]').first().click();
    cy.wait(1000);
    cy.get('[data-cy=users]').children().should('have.length', 2);
  });
});