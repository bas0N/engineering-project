import { fireEvent, render } from '@testing-library/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import axios from 'axios';
import Tiles from '../Tiles';

expect.extend(toHaveNoViolations);

jest.mock('../../../components/tiles/tile/Tile', () => ({
    ...jest.requireActual('../../../components/tiles/tile/Tile'),
    Tile: ({id}:{id: string}) => (<div>TILE ID: {id}</div>)
}));

jest.mock('../../../components/tiles/sidebar/Sidebar', () => ({
    ...jest.requireActual('../../../components/tiles/sidebar/Sidebar'),
    Sidebar: ({isOpened,
        closeSidebar
    }:{isOpened: boolean, closeSidebar: () => void}) => (<button onClick={closeSidebar}>{isOpened ? 'opened' : 'close'}</button>)
}));

jest.mock("axios");
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('Tiles', () => {

    beforeEach(() => {
        jest.clearAllMocks();

        Object.defineProperty(window, 'localStorage', {
            value: {
              getItem: jest.fn((key) => mockStorage[key] || null),
              setItem: jest.fn(),
              removeItem: jest.fn(),
              clear: jest.fn(() => {
                mockStorage = {};
              }),
            },
            writable: true,
          });
      
          let mockStorage: Record<string, string> = {
            authToken: 'testtoken'
          };
    })

    it('Should have no a11y violations', async () => {
        mockedAxios.get.mockResolvedValueOnce({
            data: {
                content: [{
                    id: "testId1",
                    boughtTogether: null,
                    categories: [],
                    description: ["loremIpsum"],
                    details: {},
                    features: [],
                    images: [],
                    averageRating: null,
                    mainCategory: null,
                    parentAsin: "testId123",
                    price: "124",
                    ratingNumber: null,
                    title: "title",
                }]
            }
        })
        const {container, getByText} = render(<Tiles />);
        expect(await axe(container)).toHaveNoViolations();
        expect(getByText('tiles.sidebarOpeningButton'));
    });

    it('Can handle the sidebar', () => {
        mockedAxios.get.mockResolvedValueOnce({
            data: {
                content: [{
                    id: "testId1",
                    boughtTogether: null,
                    categories: [],
                    description: ["loremIpsum"],
                    details: {},
                    features: [],
                    images: [],
                    averageRating: null,
                    mainCategory: null,
                    parentAsin: "testId123",
                    price: "124",
                    ratingNumber: null,
                    title: "title",
                }]
            }
        })
        const {getByText} = render(<Tiles />);
        expect(getByText('tiles.sidebarOpeningButton'));
        fireEvent.click(getByText('tiles.sidebarOpeningButton') as HTMLButtonElement);
        expect(getByText('opened'));
        fireEvent.click(getByText('opened') as HTMLButtonElement);
        expect(getByText('close'));
    });

    it('Can handle the network failure', () => {
        jest.clearAllMocks()

        const {getByText} = render(<Tiles />);
        expect(getByText('No results'));
    })

    it('Should not render anything in case the token is unprovided', () => {
        Object.defineProperty(window, 'localStorage', {
            value: {
              getItem: jest.fn((key) => mockStorage[key] || null),
              setItem: jest.fn(),
              removeItem: jest.fn(),
              clear: jest.fn(() => {
                mockStorage = {};
              }),
            },
            writable: true,
          });
          let mockStorage: Record<string, string> = {
          };
        const {queryByText} = render(<Tiles />);
        expect(queryByText('tiles.sidebarOpeningButton')).toBeFalsy();
    })
})