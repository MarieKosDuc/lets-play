import { ServerMockConnectedUserUtils } from '../utils/servermock-connected-user.utils';

describe('Annonces favorites', () => {
  beforeEach(() => {
    ServerMockConnectedUserUtils.homepageAdsMock();
    cy.loginAsUser('Test User', 'password');
  });

  it('Checks that user can add an ad to favorites/remove it from favorites', () => {
    cy.get('[data-cy=ads]').children().should('have.length', 3);
    cy.get('[data-cy=favori]').first().should('have.class', 'pi-star');
    cy.get('[data-cy=favori]').first().click();
    cy.get('[data-cy=favori]').first().should('have.class', 'pi-star-fill');
    cy.wait(1000);
    cy.get('p-toast')
      .should('be.visible')
      .should('contain', 'Annonce ajoutée aux favoris');
    cy.get('[data-cy=favori]').first().click();
    cy.get('[data-cy=favori]').first().should('have.class', 'pi-star');
    cy.wait(1000);
    cy.get('p-toast')
      .should('be.visible')
      .should('contain', 'Annonce retirée des favoris');
  });

  it("Checks that user is invited to add ads to favorites if they haven't already", () => {
    cy.get('[data-cy=nav-dropdown]').click();
    cy.get('p-menu').find('div').contains('Favorites').click();
    cy.url().should('include', '/fav-ads');
    cy.get('h1').should('contain', 'Tes annonces sauvegardées');
    cy.get('p').should(
      'contain',
      "Tu n'as sauvegardé aucune annonce pour le moment"
    );
  });

  it('Checks that user can see their favorite ads', () => {
    cy.get('[data-cy=favori]').first().click();

    cy.intercept(
      'GET',
      '/api/ads/favorites/3e4bcb7c-ce0f-4fa7-8b1d-31b8af8ad11a',
      {
        statusCode: 200,
        fixture: 'favoriteAds.json',
      }
    );

    cy.get('[data-cy=nav-dropdown]').click();
    cy.get('p-menu').find('div').contains('Favorites').click();
    cy.get('[data-cy=ads]').children().should('have.length', 1);
  });
});
