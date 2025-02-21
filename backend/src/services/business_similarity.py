from typing import List, Dict, Set, Tuple
from uuid import UUID
from src.db.models import BusinessDetails, Enrollment

class BusinessSimilarityService:
    def __init__(self, max_similar: int = 3):
        self.max_similar = max_similar

    def get_similar_businesses(self, target_business_id: UUID, businesses: List[BusinessDetails], enrollments: List[Enrollment]) -> List[BusinessDetails]:
        """Find similar businesses based on user enrollment overlap."""
        business_enrollments = self._build_enrollment_sets(businesses, enrollments)
        
        if not self._has_valid_enrollments(target_business_id, businesses, business_enrollments):
            return []

        similarities = self._calculate_similarities(target_business_id, businesses, business_enrollments)
        return self._get_top_similar(similarities)

    def _build_enrollment_sets(self, businesses: List[BusinessDetails], enrollments: List[Enrollment]) -> Dict[UUID, Set[UUID]]:
        """Create a mapping of business IDs to their enrolled user IDs."""
        business_enrollments: Dict[UUID, Set[UUID]] = {
            business.id: set() for business in businesses
        }
        
        for enrollment in enrollments:
            if enrollment.business_id in business_enrollments:
                business_enrollments[enrollment.business_id].add(enrollment.user_id)
                
        return business_enrollments

    def _has_valid_enrollments(self, target_business_id: UUID, businesses: List[BusinessDetails], 
                             business_enrollments: Dict[UUID, Set[UUID]]) -> bool:
        """Check if we can calculate similarities for the target business."""
        return (len(businesses) > 1 and 
                target_business_id in business_enrollments and 
                business_enrollments[target_business_id])

    def _calculate_jaccard_similarity(self, set1: Set[UUID], set2: Set[UUID]) -> float:
        """Calculate Jaccard similarity between two sets."""
        intersection = len(set1 & set2)
        union = len(set1 | set2)
        return intersection / union if union > 0 else 0

    def _calculate_similarities(self, target_business_id: UUID, businesses: List[BusinessDetails], 
                              business_enrollments: Dict[UUID, Set[UUID]]) -> List[Tuple[float, BusinessDetails]]:
        """Calculate similarity scores for all businesses compared to target."""
        target_users = business_enrollments[target_business_id]
        similarities = []
        
        for business in businesses:
            if business.id == target_business_id:
                continue
                
            similarity = self._calculate_jaccard_similarity(
                target_users, 
                business_enrollments[business.id]
            )
            similarities.append((similarity, business))
            
        return similarities

    def _get_top_similar(self, similarities: List[Tuple[float, BusinessDetails]]) -> List[BusinessDetails]:
        """Get top N most similar businesses."""
        similarities.sort(key=lambda x: x[0], reverse=True)
        return [business for _, business in similarities[:self.max_similar]] 