import { Image, TableRow } from "@fluentui/react-components";
import styled from "styled-components";

export const OrderHistoryTableRowWrapper = styled(TableRow)<{bgColor: string}>`
    background: ${(props) => props.bgColor};
`;

export const OrderItemsImage = styled(Image)`
    max-width: 80px;
    height: auto;
    border-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
`;