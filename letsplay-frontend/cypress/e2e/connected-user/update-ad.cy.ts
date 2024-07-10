import { ServerMockConnectedUserUtils } from '../utils/servermock-connected-user.utils';

describe("Modification d'une annonce", () => {
  beforeEach(() => {
    ServerMockConnectedUserUtils.homepageAdsMock();
    cy.loginAsUser('Test User', 'password');

    cy.get('[data-cy=ads]').children().eq(2).find('[data-cy=plus]').click();

    cy.get('[data-cy=update-ad]').click();
  });

  it('Visits the update ad page', () => {
    cy.url().should('include', '/update');
    cy.get('[data-cy=update-ad]').should('be.disabled');
  });

  it('Checks that user can open the upload image widget', () => {
    cy.get('[data-cy=upload-image]').click();
    cy.get('iframe').should('be.visible');
  });

  it('Updates the ad', () => {
    cy.get('[data-cy=title]').clear().type('Nouveau titre');
    cy.get('[data-cy=description]').clear().type('Nouvelle description');

    cy.get('[data-cy=music-styles]').find('p-multiselect').find('chevrondownicon').click();
    cy.get('[data-cy=music-styles]')
      .find('p-multiselect')
      .find('li')
      .first()
      .click();
    cy.get('[data-cy=music-styles]')
      .find('p-multiselect')
      .find('li').eq(1)
      .click();
    cy.get('[data-cy=music-styles]')
      .find('p-multiselect')
      .find('li').eq(2)
      .click();
    cy.get('[data-cy=music-styles]').find('p-multiselect').find('chevrondownicon').click();


    cy.get('[data-cy=update-ad]').click();
    cy.wait(1000);

    cy.get('p-toast').should('be.visible').should('contain', 'Annonce modifiée avec succès');
    cy.url().should('include', '/ad/1');
  });


});