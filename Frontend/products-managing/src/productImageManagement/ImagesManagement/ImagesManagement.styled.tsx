import { Image } from '@fluentui/react-components';
import { styled } from "styled-components";

export const ImagesManagementWrapper = styled.section`
    width: calc(70% - 20px);
    padding: 10px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16px;
`;

export const ImageContainer = styled.div`
    width: calc(100% - 20px);
    padding: 10px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;
    border-bottom: 1px solid #aaa;
`;

export const ImageThumb = styled(Image).attrs({shape: 'rounded'})`
    max-width: 80px;
    max-height: 80px;
`;

export const ImageOperations = styled.div`
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;
`;