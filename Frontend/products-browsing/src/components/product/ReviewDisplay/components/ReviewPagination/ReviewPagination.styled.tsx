import { Text } from '@fluentui/react-components';
import { styled } from 'styled-components';

export const ReviewPaginationWrapper = styled.div`
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
`;

export const ReviewPaginationPageDisplay = styled(Text).attrs({size: 500})`
    padding: 0px 10px;
`;