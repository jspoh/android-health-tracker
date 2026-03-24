"""rename stuff

Revision ID: a60326511213
Revises: 0275d64d6f5c
Create Date: 2026-03-24 20:39:57.509122

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = 'a60326511213'
down_revision: Union[str, Sequence[str], None] = '0275d64d6f5c'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    pass


def downgrade() -> None:
    """Downgrade schema."""
    pass
