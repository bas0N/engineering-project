import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import {axe, toHaveNoViolations} from 'jest-axe'
import { Recommendations } from '../Recommendations';
import { TileProps } from '../../../tiles/tile/Tile';

expect.extend(toHaveNoViolations);

jest.mock('../../../tiles/tile/Tile', () => ({
    ...jest.requireActual('../../../tiles/tile/Tile'),
  Tile: ({ id, title, images, averageRating, ratingNumber, price }:TileProps) => (
    <div data-testid="mock-tile">
      <p>{id}</p>
      <p>{title}</p>
      <p>{price}</p>
      <p>{averageRating}</p>
      <p>{ratingNumber}</p>
      <p>{images.length}</p>
    </div>
  ),
}));

const MOCK_PRODUCT_ID = "mockProductId";

describe('Recommendations Component', () => {

    it('has no a11y violations', async() => {
        const {container} = render(<Recommendations productId={MOCK_PRODUCT_ID} />);
        expect(await axe(container)).toHaveNoViolations();
    })

  it('renders without crashing', () => {
    render(<Recommendations  productId={MOCK_PRODUCT_ID}/>);
    expect(screen.getByText('product.recommendations')).toBeInTheDocument();
  });

  it('renders the correct number of recommendation tiles', () => {
    render(<Recommendations  productId={MOCK_PRODUCT_ID}/>);
    const tiles = screen.getAllByTestId('mock-tile');
    expect(tiles).toHaveLength(5); 
  });

  it('renders product titles correctly', () => {
    render(<Recommendations  productId={MOCK_PRODUCT_ID}/>);
    const titleElements = screen.getAllByText('$100 Mastercard Gift Card (plus $5.95 Purchase Fee)');
    expect(titleElements.length).toBe(5);
  });
});
