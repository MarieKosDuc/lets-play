import { ServerMockConnectedUserUtils } from '../utils/servermock-connected-user.utils';

describe("Profil de l'utilisateur connecté", () => {
  beforeEach(() => {
    ServerMockConnectedUserUtils.homepageAdsMock();
    cy.loginAsUser('Test User', 'password');

    cy.get('[data-cy=nav-dropdown]').click();
    cy.get('p-menu').find('div').contains('Profil').click();
  });

  it('Checks that user can see their profile', () => {
    cy.url().should('include', '/profile');
    cy.get('h1').should('contain', 'Test User');
    cy.get('[data-cy=upload-image]').should('be.visible');
    cy.get('[data-cy=ads-list]').should('be.visible').should('have.length', 1);
  });

  it('Checks that user can open the upload profile image widget', () => {
    cy.get('[data-cy=upload-image]').click();
    cy.get('iframe').should('be.visible');
  });

  it('Checks that user can update their password', () => { 
    cy.get('[data-cy=new-password]').type('ThisIsANewPassword1!');
    cy.get('[data-cy=confirm-new-password]').type('ThisIsANewPassword1!');
    cy.get('[data-cy=submit-password]').click();
    // cy.wait(1000);
    // cy.get('p-toast.p-element.ng-tns-c1067615279-0').should('contain', 'Mot de passe mis à jour');
  });

  it('Checks that user can delete their account', () => {
    cy.get('[data-cy=delete-account]').click();
    // cy.wait(1000);
    // cy.get('p-toast.p-element.ng-tns-c1067615279-5.ng-star-inserted').should('contain', 'Es-tu sûr.e de vouloir supprimer ton compte ?');
  });
});