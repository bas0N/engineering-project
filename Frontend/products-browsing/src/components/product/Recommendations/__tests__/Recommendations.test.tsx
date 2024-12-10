import { render, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import {axe, toHaveNoViolations} from 'jest-axe'
import axios from 'axios';
import { Recommendations } from '../Recommendations';
import { TileProps } from '../../../tiles/tile/Tile';

expect.extend(toHaveNoViolations);

jest.mock("axios");
const mockedAxios = axios as jest.Mocked<typeof axios>;

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

  beforeEach(() => {
    jest.clearAllMocks();
    mockedAxios.get.mockResolvedValueOnce({
      data: [{
        ids: 'testId',
        rating_number: 4.3,
        average_rating: 2.9,
        title: 'loremIpsum',
        image: 'smutna_zaba.jpg',
        price: 129
      },{
        ids: 'testId2',
        rating_number: 4.3,
        average_rating: 2.9,
        title: 'loremIpsum',
        image: 'smutna_zaba.jpg',
        price: 129
      },{
        ids: 'testId3',
        rating_number: 4.3,
        average_rating: 2.9,
        title: 'loremIpsum',
        image: 'smutna_zaba.jpg',
        price: 129
      }]
    })
  });

  it('has no a11y violations', async() => {
      const {container} = render(<Recommendations productId={MOCK_PRODUCT_ID} />);
      expect(await axe(container)).toHaveNoViolations();
  })

  it('renders without crashing', () => {
    render(<Recommendations productId={MOCK_PRODUCT_ID}/>);
    expect(screen.getByText('product.recommendations')).toBeInTheDocument();
  });

  it('renders the correct number of recommendation tiles', async () => {
    render(<Recommendations productId={MOCK_PRODUCT_ID}/>);
    await waitFor(() => {
      const tiles = screen.getAllByTestId('mock-tile');
      expect(tiles).toHaveLength(2); 
    })
  });

  it('renders product titles correctly', async() => {
    render(<Recommendations productId={MOCK_PRODUCT_ID}/>);
    await waitFor(() => {
      const titleElements = screen.getAllByText('loremIpsum');
      expect(titleElements.length).toBe(2);
    })
  });
});
