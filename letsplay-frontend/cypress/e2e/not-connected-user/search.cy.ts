import { ServerMockNotConnectedUtils } from '../utils/servermock-not-connected.utils';

describe("Fonction de recherche d'annonce", () => {
  beforeEach(() => {
    ServerMockNotConnectedUtils.homepageAdsMock();

    cy.visit('/search');
  });

  it('Visits the search page', () => {
    cy.url().should('include', '/search');
    cy.get('[data-cy=global-description]').should('not.exist')
    cy.get('h1')
      .should('be.visible')
      .should('contain', 'Recherche tes futurs musiciens');
  });

  it("Check that all search modules are visible and that form can't be submitted if empty", () => {
    cy.get('[data-cy=search-form]').should('be.visible');
    cy.get('[data-cy=musician-type-from]').should('be.visible');
    cy.get('[data-cy=musician-type-from]')
      .find('span')
      .should('contain', 'Ton instrument');
    cy.get('[data-cy=music-styles]').should('be.visible');
    cy.get('[data-cy=music-styles]')
      .find('p-multiselect')
      .should('contain', 'Sélectionne les styles de ton choix');
    cy.get('[data-cy=location]').should('be.visible');
    cy.get('[data-cy=location]')
      .find('span')
      .should('contain', 'Choisis une région');
    cy.get('button[type=submit]').should('be.visible').should('be.disabled');
  });

  it('Checks that when a band is searching for a musician, the select musician dropdown shows', () => {
    cy.get('[data-cy=musician-type-from]')
      .find('p-dropdown')
      .find('chevrondownicon')
      .click();
    cy.get('.p-dropdown-items')
      .find('li')
      .contains('Un groupe qui recherche un musicien')
      .click();
    cy.get('[data-cy=musician-type-searching]').should('be.visible');
  });

  it('Fills in the form and submits it', () => {
    cy.get('[data-cy=musician-type-from]')
      .find('p-dropdown')
      .find('chevrondownicon')
      .click();
    cy.get('.p-dropdown-items').find('li')
      .contains('Un groupe qui recherche un musicien')
      .click();
    cy.get('[data-cy=musician-type-searching]')
      .find('p-dropdown')
      .find('chevrondownicon')
      .click();
    cy.get('.p-dropdown-items').find('li').contains('Un batteur').click();
    cy.get('[data-cy=music-styles]').find('p-multiselect').click();
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
    cy.get('[data-cy=location]')
      .find('p-dropdown')
      .find('chevrondownicon')
      .click();
    cy.get('.p-dropdown-items')
      .find('li')
      .contains('Centre-Val de Loire')
      .click();
    cy.get('button[type=submit]').should('be.enabled');
  });

  it('Checks that the search results are displayed', () => {
    cy.get('[data-cy=musician-type-from]')
      .find('p-dropdown')
      .find('chevrondownicon')
      .click();
    cy.get('.p-dropdown-items')
      .find('li')
      .contains('Un groupe qui recherche un musicien')
      .click();
    cy.get('[data-cy=musician-type-searching]')
      .find('p-dropdown')
      .find('chevrondownicon')
      .click();
    cy.get('.p-dropdown-items').find('li').contains('Un batteur').click();
    cy.get('[data-cy=music-styles]').find('p-multiselect').click();
    cy.get('[data-cy=music-styles]')
      .find('p-multiselect')
      .find('li')
      .first()
      .click();
    cy.get('[data-cy=music-styles]')
      .find('p-multiselect')
      .find('li')
      .eq(1)
      .click();
    cy.get('[data-cy=music-styles]')
      .find('p-multiselect')
      .find('li')
      .eq(2)
      .click();
    cy.get('[data-cy=location]')
      .find('p-dropdown')
      .find('chevrondownicon')
      .click();
    cy.get('.p-dropdown-items')
      .find('li')
      .contains('Centre-Val de Loire')
      .click();
    cy.get('button[type=submit]').should('be.enabled').click();
    cy.wait(1000);
    cy.get('p-toast').should('be.visible').should('contain', 'Annonces trouvées');
    cy.get('[data-cy=search-results]').should('be.visible');
    cy.get('[data-cy=search-results]').children().should('have.length', 1);
  });
});
