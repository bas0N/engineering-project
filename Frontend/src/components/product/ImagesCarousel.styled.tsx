import { Image } from '@fluentui/react-components';
import styled from 'styled-components';

export const ProductPresentationImagesSection = styled.div`
    flex: 2;
    display: flex;
    gap: 8px;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    height: 100%;
`;

export const ProductPresentationImage = styled(Image)`
    max-height: 60vh;
    width: auto;
    display: block;
    margin-left: auto;
    margin-right: auto;
`;