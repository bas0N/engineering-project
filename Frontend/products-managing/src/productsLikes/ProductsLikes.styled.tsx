import { styled } from 'styled-components';

export const ProductHeader = styled.h1`
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  margin: 20px 0;
  color: #ffffff;
  padding: 16px;
  border-radius: 8px;
`;

export const ProductsWrapper = styled.section`
  width: calc(100% - 40px);
  padding: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
  border-radius: 8px;
  padding: 40px;
`;

export const ProductCard = styled.div<{ isActive: boolean }>`
  width: 260px;
  padding: 16px;
  border: 1px solid #444;
  border-radius: 8px;
  background: ${(props) => (props.isActive ? "#222" : "#2a2a2a")};
  opacity: ${(props) => (props.isActive ? 1 : 0.5)};
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  &:hover {
    transform: scale(1.05);
    box-shadow: 0 6px 12px rgba(255, 255, 255, 0.2);
  }
`;

export const ProductImage = styled.img`
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 8px;
  background-color: #ddd;
`;

export const ProductDetails = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 10px;
  padding: 10px;
`;

export const ProductTitle = styled.h3`
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  text-align: center;
  color: #fff;
`;

export const ProductInfo = styled.p`
  font-size: 14px;
  font-weight: 500;
  color: #bbb;
  margin: 2px 0;
`;

export const ProductPrice = styled.span`
  font-size: 18px;
  font-weight: bold;
  color: #ffcc00;
  padding: 6px 12px;
  border-radius: 4px;
`;

export const ProductRating = styled.span`
  font-size: 14px;
  font-weight: 400;
  color: #0080ff;
  padding: 4px 10px;
  border-radius: 4px;
`;
