import { styled } from 'styled-components';

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