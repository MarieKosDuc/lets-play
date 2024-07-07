import { ServerMockAdminUtils } from '../utils/servermock-connected-admin.utils';

describe('Interface administrateur - gestion des annonces', () => {
  beforeEach(() => {
    ServerMockAdminUtils.mockApiCalls();
    cy.loginAsAdmin('admin@mail.com', 'adminPassword');
  });

  it('Checks that admin can see the list of ads', () => {
    cy.visit('/admin/ads');
    cy.get('h1').should('contain', 'Toutes les annonces');
    cy.get('[data-cy=ads]').should('be.visible');
    cy.get('[data-cy=ads]').children().should('have.length', 3);
  });

  it('Checks that admin can view an ad', () => {
    cy.visit('/admin/ads');
    cy.get('[data-cy=ad]').first().find('[data-cy=view]').click();
    cy.url().should('contain', '/ad/3');
    cy.get('h1').should('contain', 'BATTEUR RECHERCHE GROUPE');
  });

  it('Checks that admin can delete an ad', () => {
    cy.visit('/admin/ads');
    cy.get('[data-cy=ads]').children().should('have.length', 3);
    cy.get('[data-cy=delete]').first().click();
    cy.wait(1000);
    cy.get('p-toast').should('contain', 'Es-tu sûr.e de vouloir supprimer cette annonce ?');
    cy.get('p-toast').find('[data-cy=reject]').first().click({force: true});
    cy.get('[data-cy=delete]').first().click();
    cy.wait(1000);
    cy.get('p-toast').should('contain', 'Es-tu sûr.e de vouloir supprimer cette annonce ?');
    cy.get('p-toast').find('[data-cy=confirm]').first().click({force: true});
    cy.wait(1000);
    cy.get('p-toast').should('contain', 'Annonce supprimée');

    // cy.get('[data-cy=ads]').children().should('have.length', 2);
  });
});