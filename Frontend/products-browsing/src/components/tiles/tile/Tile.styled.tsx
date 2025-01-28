import { Image, Link } from '@fluentui/react-components';
import styled from 'styled-components';

export const TileContainer = styled(Link)<{height?: number}>`
    height: calc(${(props) => props.height ?? 60}vh - 20px);
    padding: 10px;
    border-radius: 10px;
    background: #333333a0;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 8px;
    text-decoration: none !important;
    color: inherit !important;
    cursor: pointer;
    transition: filter 0.4s;

    &:hover{
        filter: brightness(70%);
    }
`;

export const TileImage = styled(Image)`
    width: 100%;
    flex: 1;
    max-height: 60%;
    width: auto;
`;
