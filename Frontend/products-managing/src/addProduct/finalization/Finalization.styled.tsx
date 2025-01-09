import { Text } from '@fluentui/react-components';
import { styled } from 'styled-components';

export const FinalizationRow = styled.div`
    width: calc(60% - 20px);
    padding: 20px 10px;
    display: flex;
    flex-direction: row;
    gap: 8px;
    align-items: top;
    justify-content: space-around;
`;

export const FinalizationTitle = styled(Text).attrs({size: 400, weight: 'semibold', align: 'start'})`
    width: calc(60% - 20px);
`;

export const FinalizationValue = styled(Text)`
    flex: 1;
`;

export const FinalizationContent = styled.div<{flexDirection: string}>`
    flex: 1;
    display: flex;
    flex-direction: ${(props) => props.flexDirection};
    gap: 8px;
    align-items: center;
`;

export const FinalizationContentRow = styled.div`
    display: flex;
    gap: 8px;
    align-items: center;
    justify-content: space-around;
`;

export const FinalizationContentText = styled(Text).attrs({align: 'center', size: 400})`
    width: calc(100% - 10px);
    padding: 5px;
`;