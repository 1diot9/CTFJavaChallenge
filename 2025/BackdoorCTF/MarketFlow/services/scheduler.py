import secrets
import json
import os
from datetime import datetime
from models.database import get_db
from services.cache_service import CacheService, CacheConfiguration
from models.analytics import ReportConfiguration 

class Scheduler:
    def __init__(self):
        pass
    
    def schedule_task(self, task_data):
        db = get_db()
        task_id = secrets.token_hex(16)
        
        db.execute(
            "INSERT INTO scheduled_tasks (id, task_type, task_data, status) VALUES (?, ?, ?, ?)",
            [task_id, task_data.get('task_type'), json.dumps(task_data), 'pending']
        )
        db.commit()
        
        return task_id
    
    def process_pending(self):
        from services.renderer import TemplateRenderer
        from services.object_manager import ObjectManager
        
        db = get_db()
        pending = db.execute(
            "SELECT * FROM scheduled_tasks WHERE status = 'pending' ORDER BY scheduled_at ASC LIMIT 10"
        ).fetchall()
        
        renderer = TemplateRenderer()
        manager = ObjectManager()
        cache_service = CacheService()
        
        for task in pending:
            task_data = json.loads(task['task_data'])
            task_type = task_data.get('task_type')
            
            try:
                if task_type == 'generate_report':
                    config_data = task_data.get('config', {})
                    config_obj = manager.deserialize(config_data)
                    report_token = task_data.get('report_token', secrets.token_urlsafe(32))

                    if isinstance(config_obj, ReportConfiguration):
                        if hasattr(config_obj, 'processor') and config_obj.processor:
                            processor = config_obj.processor
                            
                            if hasattr(processor, 'output_config') and processor.output_config:
                                output_cfg = processor.output_config
                                
                                if isinstance(output_cfg, CacheConfiguration):
                                    cache_service.prime(output_cfg)

                    if hasattr(config_obj, 'template') and config_obj.template:
                        template_spec = config_obj.template
                        if isinstance(template_spec, dict) and '_type' in template_spec:
                            template_spec = manager.deserialize(template_spec)
                        
                        if hasattr(template_spec, 'template_name'):
                            output = renderer.render(template_spec)
                            
                            output_path = f'/var/tmp/sessionmaze/reports/{report_token}.html'
                            
                            if hasattr(template_spec, 'output_path') and template_spec.output_path:
                                custom_output = template_spec.output_path
                                if not custom_output.startswith('/'):
                                    output_path = os.path.join('/var/tmp/sessionmaze/reports', custom_output)
                                else:
                                    output_path = custom_output
                            
                            os.makedirs(os.path.dirname(output_path), exist_ok=True)
                            with open(output_path, 'w') as f:
                                f.write(output)
                
                db.execute(
                    "UPDATE scheduled_tasks SET status = ?, processed_at = ? WHERE id = ?",
                    ['completed', datetime.now().isoformat(), task['id']]
                )
                db.commit()
                
            except Exception as e:
                db.execute(
                    "UPDATE scheduled_tasks SET status = ? WHERE id = ?",
                    [f'failed: {str(e)}', task['id']]
                )
                db.commit()
    
    def get_task_status(self, task_id):
        db = get_db()
        task = db.execute("SELECT * FROM scheduled_tasks WHERE id = ?", [task_id]).fetchone()
        
        if task:
            return dict(task)
        return None