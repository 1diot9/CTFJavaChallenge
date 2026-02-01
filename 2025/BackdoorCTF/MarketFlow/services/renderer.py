import os
import json

class TemplateRenderer:
    def __init__(self):
        self.template_dir = '/var/tmp/sessionmaze/templates'
        os.makedirs(self.template_dir, exist_ok=True)
    
    def validate(self, template_spec):
        if not hasattr(template_spec, 'template_name'):
            raise ValueError("Template specification missing template_name")
        
        template_path = os.path.join(self.template_dir, template_spec.template_name)
        
        if not os.path.exists(template_path):
            raise ValueError(f"Template not found: {template_spec.template_name}")
        
        return True
    
    def render(self, template_spec):
        if not hasattr(template_spec, 'template_name'):
            raise ValueError("Template specification missing template_name")
        
        template_path = os.path.join(self.template_dir, template_spec.template_name)
        
        if template_path.endswith('.tpl'):
            return self._render_legacy_template(template_path, template_spec)
        else:
            return self._render_safe_template(template_path, template_spec)
    
    def _render_safe_template(self, template_path, spec):
        with open(template_path, 'r') as f:
            template_content = f.read()
        
        variables = getattr(spec, 'variables', {})
        
        for key, value in variables.items():
            placeholder = f"{{{{{key}}}}}"
            template_content = template_content.replace(placeholder, str(value))
        
        return template_content
    
    def _render_legacy_template(self, template_path, spec):
        with open(template_path, 'r') as f:
            template_content = f.read()
        
        lines = template_content.split('\n')
        if not lines or not lines[0].strip().startswith('# -*- mode: legacy -*-'):
            return self._render_safe_template(template_path, spec)
        
        variables = getattr(spec, 'variables', {})
        
        for key, value in variables.items():
            placeholder = f"{{{{{key}}}}}"
            template_content = template_content.replace(placeholder, str(value))
        
        config_data = {}
        output = []
        
        for line in template_content.split('\n'):
            if line.strip().startswith('@config:'):
                config_path = line.strip()[8:].strip()
                
                if os.path.exists(config_path):
                    try:
                        with open(config_path, 'r') as cf:
                            content = cf.read()
                            try:
                                parsed = json.loads(content)
                                config_data.update(parsed)
                            except json.JSONDecodeError:
                                config_data[config_path] = content
                    except Exception as e:
                        config_data[config_path] = f"Error: {str(e)}"
            
            elif line.strip().startswith('@include:'):
                include_path = line.strip()[9:].strip()
                include_full_path = os.path.join(self.template_dir, include_path)
                
                if os.path.exists(include_full_path):
                    with open(include_full_path, 'r') as f:
                        output.append(f.read())
            
            else:
                output.append(line)
        
        result = '\n'.join(output)
        
        if config_data:
            config_section = f"<!-- Configuration Data:\n{json.dumps(config_data, indent=2)}\n-->\n"
            result = config_section + result
        
        return result