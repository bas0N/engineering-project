import { Table, Text } from "@fluentui/react-components";
import styled from "styled-components";

export const OrderHistoryContainer = styled.section`
    max-width: 800px;
    margin: 40px auto;
    background: #1e1e1e;
    color: #f1f1f1;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
    border-radius: 10px;
    padding: 20px;
`;

export const OrderHistoryHeader = styled(Text)`
    margin-bottom: 30px;
    font-size: 24px;
    font-weight: 600;
    color: #ffffff;
`;

export const OrderHistoryCard = styled.div`
    background: #2a2a2a;
    border-radius: 10px;
    color: #f1f1f1;
    padding-bottom: 25px;
`;

export const OrderHistoryCardHeader = styled(Text)`
    padding: 15px 20px;
    font-size: 16px;
    font-weight: 600;
    color: #dcdcdc;
`;

export const OrderHistoryCardContent = styled.div`
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 15px;
`;

export const OrderHistoryTable = styled(Table)`
    width: 100%;
    border-collapse: collapse;
    margin: 10px 0;
`;

export const OrderHistoryFooter = styled(Text)`
    padding: 15px 20px;
    border-top: 1px solid #444;
    text-align: right;
    font-weight: 600;
    color: #f1f1f1;
`;
