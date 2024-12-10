import styled from 'styled-components';
import { Text } from '@fluentui/react-components';

export const ReviewDisplayWrapper = styled.section`
    width: calc(100vw - 20px);
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const ReviewDisplayContainer = styled.div`
    width: calc(100% - 20px);
    padding: 10px;
    padding-bottom: 20px;
    display: flex;
    flex-direction: column;
    gap: 6px;

    @media screen and (min-width: 768px){
        width: calc(80% - 20px);
    }
`;

export const ReviewTitleWrapper = styled.div`
    display: flex;
    gap: 12px;
    align-items: center;
`;

export const ReviewPaginationWrapper = styled.div`
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
`;

export const ReviewPaginationPageDisplay = styled(Text).attrs({size: 500})`
    padding: 0px 10px;
`;