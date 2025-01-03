import { Text } from "@fluentui/react-components";
import styled from "styled-components";

export const OrderSummaryContainer = styled.div`
    display: flex;
    flex-direction: column;
    gap: 4px;
    padding: 36px 16px;
    border-radius: 8px
`;

export const OrderLabel = styled(Text)`
    font-weight: 600;
`;
